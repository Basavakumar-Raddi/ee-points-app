import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Http, Headers, RequestOptions, URLSearchParams } from "@angular/http";
import { map } from 'rxjs/operators';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class PointService {

  constructor(private aHttp : Http, private http : HttpClient) { }

  private pointBaseUrl = 'http://localhost:8080/api/v1/points';
  

  private headers = new Headers({ 'Content-Type': 'application/json'});     
  private options = new RequestOptions({headers: this.headers});

  public getPointsSummary() {
    return this.aHttp.get(this.pointBaseUrl+'/summary', this.options).pipe(map(response => {
        {
          return response.json(); 
        };
    }));
  }

  public getPointsPaginated(offset, limit) {
    let params: URLSearchParams = new URLSearchParams();
    params.set('offset', offset);
    params.set('limit', limit); 
    this.options.params = params;
    return this.aHttp.get(this.pointBaseUrl, this.options).pipe(map(response => {
        {
          return response.json(); 
        };
    }));
  }

   public deleteUser(id) {
    return this.aHttp.delete(this.pointBaseUrl+'?id='+id,this.options).pipe(map(response => {
        {
          return response.json(); 
        };
    }));
  }

}
