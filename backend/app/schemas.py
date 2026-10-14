from pydantic import BaseModel


class PromotionCreate(BaseModel):
    City: str
    Category: str
    Name: str
    Type: str
    Promotional: str
    Time_of_application: str
    Address: str
    Link_to_website: str