import sqlite3
import socket
import threading
import json

CREATE_TABLE_QUERY = """CREATE TABLE IF NOT EXISTS players(Username text UNIQUE, Password text, HighestScore text DEFAULT '0', 
Wins text DEFAULT '0')"""


def new_connection(sock, ip_address):
    print(f"new connection has received from {ip_address}")
    db_connection = sqlite3.connect('database.sql', check_same_thread=False)
    db_cursor = db_connection.cursor()
    client_request = sock.recv(1024).decode()
    response = request_handler(db_connection, db_cursor, json.loads(client_request))
    sock.send(response.encode())
    print(f"sent {response}")
    sock.close()
    db_connection.close()
    print(f"closed connection with {ip_address}\n")
    return response


def request_handler(connection, cursor, request):
    if request["request"] == "get_players_by_wins":
        print("Getting players ordered by Wins")
        get_players_by_wins = f"SELECT Username, HighestScore, Wins FROM players ORDER BY Wins DESC, HighestScore DESC"
        try:
            cursor.execute(get_players_by_wins)
            players_order = cursor.fetchmany(20)  # returns the first 20 users by most wins and highest score
            connection.commit()
            print(players_order)
            return json.dumps({"response": "true", "players_order": players_order})
        except Exception as e:
            print(e)
        return json.dumps({"response": "false"})

    elif request["request"] == "update_last_game_data":
        username = request["username"]
        is_win = request["is_win"]
        score = request["score"]
        print("Updating data")
        try:
            if is_win == "true":
                get_wins_query = f"SELECT Wins FROM players WHERE Username = '{username}'"
                cursor.execute(get_wins_query)
                wins = int(cursor.fetchone()[0])
                connection.commit()
                update_wins_query = f"UPDATE players SET Wins = '{wins + 1}' WHERE Username = '{username}'"
                cursor.execute(update_wins_query)
                connection.commit()
            get_highest_score_query = f"SELECT HighestScore FROM players WHERE Username = '{username}'"
            cursor.execute(get_highest_score_query)
            highest_score = cursor.fetchone()[0]
            connection.commit()
            update_score_query = f"UPDATE players SET HighestScore = '{score}' WHERE Username = '{username}' AND {highest_score} < {score}"
            cursor.execute(update_score_query)
            connection.commit()
            return json.dumps({"response": "true"})
        except Exception as e:
            print(e)
        return json.dumps({"response": "false"})

    elif request["request"] == "register":
        username = request["username"]
        password = request["password"]
        insert_data_query = f"INSERT INTO players (Username, Password) VALUES ('{username}', '{password}')"
        try:
            cursor.execute(insert_data_query)
            connection.commit()
            print("Registering")
            return json.dumps({"response": "true"})
        except Exception as e:
            print(e)
        return json.dumps({"response": "false"})

    elif request["request"] == "login":
        username = request["username"]
        password = request["password"]
        data_exists_query = f"SELECT * FROM players WHERE Username = '{username}' AND Password = '{password}'"
        try:
            cursor.execute(data_exists_query)
            data = cursor.fetchone()
            connection.commit()
            if data is not None:
                print("Logging in")
                return json.dumps({"response": "true"})
        except Exception as e:
            print(e)
        return json.dumps({"response": "false"})


def main():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(("0.0.0.0", 1225))
    print("server started")
    server_socket.listen()

    with sqlite3.connect('database.sql', check_same_thread=False) as connection:
        cursor = connection.cursor()
        cursor.execute(CREATE_TABLE_QUERY)
        connection.commit()

    while True:
        new_client = server_socket.accept()
        new_thread = threading.Thread(target=new_connection, args=new_client)
        new_thread.start()


if __name__ == '__main__':
    main()
