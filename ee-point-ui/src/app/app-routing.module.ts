import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ViewComponentComponent } from './view-component/view-component.component'

const routes: Routes = [
  { path: 'view', component: ViewComponentComponent },
  { path: '', component: ViewComponentComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
