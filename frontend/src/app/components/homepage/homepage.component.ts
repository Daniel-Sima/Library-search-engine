import { HttpClient } from '@angular/common/http';
import { Component, Renderer2 } from '@angular/core';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],
})
export class HomepageComponent {
  [x: string]: any;
  // Variable qui contient le texte de recherche
  searchText: string = '';

  // Variable qui indique si l'etat de la recherche
  searchStatus = 0; // 0: pas de recherche, 1: recherche en cours, 2: recherche OK, 3: recherche KO

  // Variable qui continent les livres recherches
  books: any = undefined;

  /***************************************************************************************/
  constructor(private http: HttpClient, private renderer: Renderer2) {}

  /***************************************************************************************/
  /**
   * Function to get the search text.
   */
  getSearch() {
    if (this.searchText !== '') {
      this.searchStatus = 1;
      console.log('searching: ', this.searchText);
      var encoded = encodeURIComponent(this.searchText);
      this.http.get(`http://localhost:8080/api/search/${encoded}`).subscribe(
        (data: any) => {
          this.books = data;
          console.log('books: ', this.books);
          if (this.books.length === 0) {
            this.searchStatus = 3;
          } else {
            this.searchStatus = 2;
          }
        },
        (error) => {
          console.error(error.error);
          this.searchStatus = 3;
        }
      );
    }
  }
}
