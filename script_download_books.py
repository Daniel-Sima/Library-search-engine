import requests


guntendexURL = "https://gutendex.com/books/"
url_book = "https://www.gutenberg.org/cache/epub/47/pg47.txt"

# Get the list of books
response = requests.get(guntendexURL)
nb_words = 0
nb_texts_downloaded = 543
i = 616

while nb_texts_downloaded < 1664:
    url_book = "https://www.gutenberg.org/cache/epub/"+str(i)+"/pg"+str(i)+".txt"
    response = requests.get(url_book)
    if(response.status_code == 200):
        if(response.text == "Not Found"):
            print("Book "+str(i)+" not found")
            continue

        words = response.text.split()
        nb_words = len(words)
        print("Book "+str(i)+" has "+str(nb_words)+" words")
        if nb_words > 10000:
            with open("books/book"+str(i)+".txt", "w") as f:
                f.write(response.text)
            print("Book "+str(i)+" downloaded")
            nb_texts_downloaded += 1
        else:
            print("Book "+str(i)+" too short")
    else:
        print("Book "+str(i)+" not found")
    i += 1