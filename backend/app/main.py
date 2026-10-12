from fastapi import FastAPI
from app.database import create_table
from app.routers.promotions import router as promotions_router
from fastapi.middleware.cors import CORSMiddleware


create_table()

app = FastAPI(title='Агрегатор акций')

app = FastAPI()


app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(promotions_router, prefix='/api', tags=['promotions'])


@app.get('/')
def root():
    return {'message': 'API работает'}