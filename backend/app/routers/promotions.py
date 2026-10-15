from app import crud
from app.schemas import PromotionCreate
from typing import List, Optional
from fastapi import APIRouter, HTTPException


router = APIRouter()


@router.get('/promotions')
def list_promotions(City: Optional[str] = None, Type: Optional[str] = None, Category: Optional[str] = None):
    return crud.get_promotions(City=City, Type=Type, Category=Category)


@router.get('/promotions/{id}')
def get_promotion(id: int):
    promo = crud.get_id_from_promotion(id)
    if not promo:
        raise HTTPException(status_code=404, detail='Акция не найдена')
    return promo


@router.post('/promotions')
def add_promotion(data: PromotionCreate): 
    id = crud.create_promotion(data.dict()) 
    return {'id': id, 'status': 'created'}


@router.put('/promotions/{id}')
def update_promo(id: int, data: PromotionCreate):
    if not crud.get_id_from_promotion(id):
        raise HTTPException(status_code=404, detail='Акция не найдена')
    return crud.update_promotion(id, data.dict())

@router.delete('/promotions/{id}')
def delete_promo(id: int):
    if not crud.get_id_from_promotion(id):
        raise HTTPException(status_code=404, detail='Акция не найдена')
    return crud.delete_promotion(id)


@router.get('/categories', response_model=List[str])
def list_categories():
    return crud.get_categories()