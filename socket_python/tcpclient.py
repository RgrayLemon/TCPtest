# -*- coding: utf-8 -*-
"""
Created on Sat Sep 25 20:26:47 2021

@author: hiromi
"""

# ソケット通信(クライアント側)
import socket
from PIL import Image
import socketserver
import io

ip1 = '192.168.3.2'
#ip1 = '10.2.0.0'
port1 = 2001
server1 = (ip1, port1)

socket1 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#接続
socket1.connect(server1)

img = Image.open('atmr.PNG')


img_bytes = io.BytesIO()
img.save(img_bytes,format="PNG")
img_bytes = img_bytes.getvalue()

print(len(img_bytes))  #len(img_bytes)でバイトサイズを取得する

byteSize = len(img_bytes)
#sendByte = bytearray(byteSize)
#sendByte.append(img_bytes)

socket1.send(img_bytes) #<-画像を送信
#self.request(123)

print("送信")

"""
line = ''
while line != 'bye':
    # 標準入力からデータを取得
    print('偶数の数値を入力して下さい')
    line = input('>>>')
    
    # サーバに送信
    socket1.send(line.encode("UTF-8"))
    
    # サーバから受信
    data1 = socket1.recv(4096).decode()
    
    # サーバから受信したデータを出力
    print('サーバーからの回答: ' + str(data1))

socket1.close()
print('クライアント側終了です')
"""