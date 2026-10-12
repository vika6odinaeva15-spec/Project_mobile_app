import csv
import sqlite3
import sys
import os

sys.path.append(os.path.abspath("."))

from app.database import DB_PATH, create_table

create_table()

conn = sqlite3.connect(DB_PATH)
cursor = conn.cursor()

with open("data/promotions.csv", "r", encoding="utf-8") as f:
    reader = csv.DictReader(f, delimiter=";") 
    count = 0
    for row in reader:
        cursor.execute("""
            INSERT INTO promotions
            (City, Category, Name, Type, Promotional, Time_of_application, Address, Link_to_website)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            row["City"],
            row["Category"],
            row["Name"],
            row["Type"],
            row["Promotional"],
            row.get("Time_of_application"),
            row["Address"],
            row.get("Link_to_website"),
        ))
        count += 1

conn.commit()
conn.close()
print(f"Загружено {count} записей")