from PIL import Image
import socketserver
import io

#ひとまず同じ階層に置いた
img = Image.open('atmr.PNG')


img_bytes = io.BytesIO()
img.save(img_bytes,format="PNG")
img_bytes = img_bytes.getvalue()

print(len(img_bytes))  #len(img_bytes)でバイトサイズを取得する


class MyTCPHandler(socketserver.BaseRequestHandler):
    def handle(self):
        self.data = self.request.recv(1024).strip()
        print("{} wrote:".format(self.client_address[0]))
        print(self.data)
        
        byteSize = len(img_bytes)
        #sendByte = bytearray(byteSize)
        #sendByte.append(img_bytes)
        
        self.request.sendall(img_bytes) #<-画像を送信
        #y = bytearray([0x01,0x02,0x03,0x04])
        #print(y)
        #self.request.sendall(y)

if __name__ == "__main__":
    HOST, PORT = '192.168.3.10', 2001

    with socketserver.TCPServer((HOST, PORT), MyTCPHandler) as server:
        server.serve_forever()
        