import sqlite3
import os


DB_PATH = 'data/promotions.db'
os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)


def get_db():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    try:
        yield conn
    finally:
        conn.close()


def create_table():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS promotions (
           id INTEGER PRIMARY KEY AUTOINCREMENT,
           City VARCHAR(100) NOT NULL,
           Category VARCGAR(150) NOT NULL,
           Name VARCHAR(250) NOT NULL,
           Type VARCHAR(60) NOT NULL,
           Promotional TEXT NOT NULL,
           Time_of_application TEXT NOT NULL,
           Address VARCHAR(200) NOT NULL,
           Link_to_website TEXT
        )
    ''')
    conn.commit()
    conn.close()
