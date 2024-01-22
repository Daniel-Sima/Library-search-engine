import json
import requests
from rest_framework.views import APIView
from rest_framework.response import Response
from mygutenberg.config import baseUrl, baseUrlBooks
      
class RedirectionListeDeBooks(APIView):
  def get(self, request, format=None):
    response = requests.get(baseUrlBooks + "books/")
    if response.status_code == 200:
      try:
        json_data = response.json()
        return Response(json_data)
        
      except json.JSONDecodeError as e:
        return Response({"error": f"JSON decoding failed with exception: {str(e)}"})
    else:
      print(f"Error request: {response.status_code}")

class RedirectionDetailBook(APIView):
    def get(self, request, pk, format=None):
        try:
            response = requests.get(baseUrlBooks+'books/'+str(pk)+'/')
            jsondata = response.json()
            return Response(jsondata)
        except:
            raise Http404
          
class RedirectionFrenchBooks(APIView):
    def get(self, request, format=None):
        try:
            response = requests.get(baseUrlBooks+'books/?languages=fr')
            jsondata = response.json()
            return Response(jsondata)
        except:
            raise Http404
          
class RedirectionDetailFrenchBooks(APIView):
    def get(self, request, pk, format=None):
        try:
            response = requests.get(baseUrlBooks+'books/'+str(pk)+'/?languages=fr')
            jsondata = response.json()
            return Response(jsondata)
        except:
            raise Http404
      
class RedirectionEnglishBooks(APIView):
    def get(self, request, format=None):
        try:
            response = requests.get(baseUrlBooks+'books/?languages=en')
            jsondata = response.json()
            return Response(jsondata)
        except:
            raise Http404
          
class RedirectionDetailEnglishBooks(APIView):
    def get(self, request, pk, format=None):
        try:
            response = requests.get(baseUrlBooks+'books/'+str(pk)+'/?languages=en')
            jsondata = response.json()
            return Response(jsondata)
        except:
            raise Http404
               
          
          