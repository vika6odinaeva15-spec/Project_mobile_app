import sqlite3
from app.database import DB_PATH


def get_promotions(City: str = None, Type: str = None, Category: str = None):
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()

    query = 'SELECT * FROM promotions WHERE 1=1' 
    params = []

    if City:
        query += ' AND City=?' 
        params.append(City)
    if Type:
        query += ' AND Type=?'
        params.append(Type)
    if Category:
        query += ' AND Category=?'
        params.append(Category)

    cursor.execute(query, params)
    rows = [dict(row) for row in cursor.fetchall()]
    conn.close()
    return rows


def get_id_from_promotion(id: int):
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    cursor = conn.cursor()
    cursor.execute('SELECT * FROM promotions WHERE id=?', (id,))
    row = cursor.fetchone()
    conn.close()
    return dict(row) if row else None


def create_promotion(data: dict):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        INSERT INTO promotions
        (City, Category, Name, Type, Promotional, Time_of_application, Address, Link_to_website)
        VALUES (?,?,?,?,?,?,?,?)
    ''', (
        data['City'],
        data['Category'],
        data['Name'],
        data['Type'],
        data['Promotional'],
        data['Time_of_application'],
        data['Address'],
        data['Link_to_website'],
    ))
    conn.commit()
    id = cursor.lastrowid
    conn.close()
    return id

def update_promotion(id: int, data: dict):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        UPDATE promotions
        SET City=?, Category=?, Name=?, Type=?, Promotional=?, Time_of_application=?, Address=?, Link_to_website=?
        WHERE id=?
    ''', (data['City'], data['Category'], data['Name'], data['Type'], data['Promotional'], data['Time_of_application'], data['Address'], data['Link_to_website'], id))
    conn.commit()
    conn.close()
    return {"status": "updated"}

def delete_promotion(id: int):
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('DELETE FROM promotions WHERE id=?', (id,))
    conn.commit()
    conn.close()
    return {"status": "deleted"}

def get_categories():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute("SELECT DISTINCT category FROM promotions ORDER BY category")
    rows = [row[0] for row in cursor.fetchall()]
    conn.close()
    return rows