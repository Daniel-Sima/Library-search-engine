import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component, Renderer2 } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-book',
  templateUrl: './book.component.html',
  styleUrls: ['./book.component.css'],
})
export class BookComponent {
  // Book ID
  bookId: number;

  // Books DTOs
  books: any = undefined;

  // Neighboors DTOs (Jaccard Similarity)
  neighboors: any = undefined;

  // Loading state
  loaded = false;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private renderer: Renderer2,
    private cdr: ChangeDetectorRef
  ) {
    // Récupérez l'ID du livre à partir des paramètres de l'URL
    const id = this.route.snapshot.paramMap.get('id');
    this.bookId = id ? +id : 0;

    console.log('bookId: ', this.bookId);
    if (this.bookId != 0) {
      this.http.get(`http://localhost:8080/api/book/${id}`).subscribe(
        (data: any) => {
          this.books = data;
          this.neighboors = this.books.slice(1);
          this.loaded = true;
        },
        (error) => {
          console.error(error.error);
        }
      );
    }
  }

  /*************************************************************************************/
  onBookClick(id: number) {
    console.log('bookId: ', id);
    if (this.bookId != 0) {
      this.http.get(`http://localhost:8080/api/book/${id}`).subscribe(
        (data: any) => {
          this.books = data;
          this.neighboors = this.books.slice(1);
          console.log('books: ', this.books);
          console.log('neighboors: ', this.neighboors && this.neighboors);

          // Forcer la détection des changements
          this.cdr.detectChanges();
        },
        (error) => {
          console.error(error.error);
        }
      );
    }
  }
}
