from rest_framework.serializers import ModelSerializer
from mytig.models import ProduitEnPromotion
from mytig.models import ProduitAvailable

class ProduitEnPromotionSerializer(ModelSerializer):
    class Meta:
        model = ProduitEnPromotion
        fields = ('id', 'tigID')

class ProduitAvailableSerializer(ModelSerializer):
    class Meta:
        model = ProduitAvailable
        fields = ('id', 'tigID')