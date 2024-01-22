from rest_framework.serializers import ModelSerializer
from models import Books

class BooksSerializer(ModelSerializer):
    class Meta:
        model = Books
        fields = ('id', 'tigID')
    
class ProduitEnPromotionSerializer(ModelSerializer):
    class Meta:
        model = ProduitEnPromotion
        fields = ('id', 'tigID')
