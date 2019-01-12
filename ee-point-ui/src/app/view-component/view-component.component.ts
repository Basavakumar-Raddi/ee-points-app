import { Component, OnInit, AfterViewInit, TemplateRef } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/bs-modal-ref.service';
import { GridOptions, IDatasource, IGetRowsParams, GridApi } from 'ag-grid-community';
import {PointService } from '../point.service';
import * as _ from 'lodash';

@Component({
  selector: 'app-view-component',
  templateUrl: './view-component.component.html',
  styleUrls: ['./view-component.component.css']
})
export class ViewComponentComponent implements OnInit, AfterViewInit {
  gridOptions: GridOptions;
  private gridApi;
  private gridColumnApi;
  private columnDefs;
  private components;
  private rowBuffer;
  private rowSelection;
  private rowModelType;
  private paginationPageSize;
  private cacheOverflowSize;
  private maxConcurrentDatasourceRequests;
  private infiniteInitialRowCount;
  private maxBlocksInCache;
  private cacheBlockSize;
  rowSelected: boolean = false;
  minValue: number = 0;
  averageValue: number = 0;
  maxValue: number = 0;
  totalValue: number = 0;
  errorMsg: string = '';
  successMsg: string = '';
  errorFlag: boolean = false;
  successFlag: boolean = false;
  showSuccessMsg: boolean = true;
  response: Object = null;

  constructor(private pointService : PointService, private modalService: BsModalService) { 
    this.columnDefs  = [
      {headerName: 'MeasurementDay', field: 'measurementDay', suppressFilter: true},
      {headerName: 'MeasurementLocation', field: 'location', filter: "agTextColumnFilter", 
        filterParams: {
          filterOptions: ["equals", "notEqual"],
          newRowsAction: "keep"
        }
      },
      {headerName: 'MeasurementValue', field: 'value', suppressFilter: true}
    ];
    this.rowBuffer = 0;
    this.rowSelection = "single";
    this.rowModelType = "infinite";
    this.paginationPageSize = 10;
    this.cacheOverflowSize = 2;
    this.maxConcurrentDatasourceRequests = 1;
    this.infiniteInitialRowCount = 20;
    this.maxBlocksInCache = 2;
    this.cacheBlockSize = 10;
  }

  ngOnInit() {
    this.pointSummary();
  }

  ngAfterViewInit(): void {}

  dataSource: IDatasource = {
    getRows: (params: IGetRowsParams) => {
      this.pointService.getPointsPaginated(params.startRow, this.cacheBlockSize).subscribe(data => {
        console.log("asking for " + params.startRow + " to " + params.endRow);
            setTimeout((function() {
              var dataAfterSortingAndFiltering = this.sortAndFilter(data.response.pointDTOS, params.sortModel, params.filterModel);
              var rowsThisPage = dataAfterSortingAndFiltering;
              var lastRow = -1;
              if (data.response.totalRows <= params.endRow) {
                lastRow = data.response.totalRows;
              }
              params.successCallback(rowsThisPage, lastRow);
            }).bind(this), 100);
      })
    }
  }

  onGridReady(params) {
    this.gridApi = params.api;
    this.gridColumnApi = params.columnApi;
    this.gridApi.setDatasource(this.dataSource);
  }

  deletePoint() {
    var id=this.gridApi.getSelectedRows();
    console.log(id);
    if(id){
      this.pointService.deleteUser(id[0]['id']).subscribe(response => {
        if (!response.success) {
          this.errorFlag = true;
          this.errorMsg = response.message;
          alert(this.errorMsg);
        } else {
          this.successFlag = true;
          this.showSuccessMsg = false;
          this.successMsg = response.message;
          alert(this.successMsg );
          this.pointSummary();
          this.gridApi.setDatasource(this.dataSource);
        }
      }, error => {
        this.errorFlag = true;
        this.errorMsg = "delete failed";
      });
    }
  }

  onSelectionChanged(){
    this.rowSelected = true
  }

  pointSummary() {
    this.pointService.getPointsSummary().subscribe(response => {
      if (!response.success) {
        this.errorFlag = true;
        this.errorMsg = response.message;
      } else {
        this.response = response.response;
        this.minValue = response.response.minValue;
        this.averageValue = response.response.averageValue;
        this.maxValue = response.response.maxValue;
        this.totalValue = response.response.totalValue;
        this.successFlag = true;
        this.showSuccessMsg = false;
        this.successMsg = response.message;
      }
    }, error => {
      this.errorFlag = true;
      this.errorMsg = "Data Fetch failed";
    });
  }

  sortAndFilter(allOfTheData, sortModel, filterModel) {
    return this.sortData(sortModel, this.filterData(filterModel, allOfTheData));
  }

 sortData(sortModel, data) {
    var sortPresent = sortModel && sortModel.length > 0;
    if (!sortPresent) {
      return data;
    }
    var resultOfSort = data.slice();
    resultOfSort.sort(function(a, b) {
      for (var k = 0; k < sortModel.length; k++) {
        var sortColModel = sortModel[k];
        var valueA = a[sortColModel.colId];
        var valueB = b[sortColModel.colId];
        if (valueA == valueB) {
          continue;
        }
        var sortDirection = sortColModel.sort === "asc" ? 1 : -1;
        if (valueA > valueB) {
          return sortDirection;
        } else {
          return sortDirection * -1;
        }
      }
      return 0;
    });
    return resultOfSort;
  }

 filterData(filterModel, data) {
    var filterPresent = filterModel && Object.keys(filterModel).length > 0;
    if (!filterPresent) {
      return data;
    }
    var resultOfFilter = [];
    for (var i = 0; i < data.length; i++) {
      var item = data[i];
      if (filterModel.location) {
        var location = item.location;
        var str = filterModel.location.filter;
        var allowedLoc = str.toUpperCase();
        if (filterModel.location.type == "equals") {
          if (location !== allowedLoc) {
            continue;
          }
        } else {
          if (location <= allowedLoc) {
            continue;
          }
        }
      }
      resultOfFilter.push(item);
    }
    return resultOfFilter;
  }

}
