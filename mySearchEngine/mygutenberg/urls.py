from django.urls import path
from mygutenberg import views

urlpatterns = [
    path('books/', views.RedirectionListeDeBooks.as_view()),
    path('books/<int:pk>', views.RedirectionDetailBook.as_view()),
    path('frenchbooks/', views.RedirectionFrenchBooks.as_view()),
    path('frenchbooks/<int:pk>', views.RedirectionDetailFrenchBooks.as_view()),
    path('englishbooks/', views.RedirectionEnglishBooks.as_view()),
    path('englishbooks/<int:pk>', views.RedirectionDetailEnglishBooks.as_view()),
]
