import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-bookdetails',
  templateUrl: './bookdetails.component.html',
  styleUrls: ['./bookdetails.component.css'],
})
export class BookdetailsComponent {
  // Input pour recevoir les details du livre du composant parent
  @Input() book: any;

  // Input permettant d'afficher le lien de lecture
  @Input()
  idConcerned!: number;

  /*************************************************************************************/
  isBookRoute() {
    return window.location.pathname.includes('book/');
  }
}
