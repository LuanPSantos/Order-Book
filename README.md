# Meli Order-Book

Em um Order Book ou Livro de ofertas contém as ofertas de compra e venda para um determinado ativo (Vibranium).
Ao realizar uma oferta de venda, a quantidade ofertada é subtraída da carteira e entra em trade com valor requerido pelo vendedor.
Ao realizar uma oferta de compra, o total em dinheiro (tamanho da oferta x preço desejado de compra) é subtraído da carteira e entra em trade.
Quando um ofeta de compra encontra uma ofeta de venda compatível (ou vice-versa), ocorre uma transação e os valores correpondetes são depositados nas carteiras envolvidas:
é adicionado a quantiade de vibranium na carteira do comprador; é adicionado o valor total da transação na carteira do vendedor. Também são salvos os dados com detalhes sobre a transação.

## Rodando a aplicação

**1 - Faça do clone deste repositório**  

``
git clone https://github.com/LuanPSantos/Meli-Order-Book.git
``

**2 - Entre no diretório "/order-book"** 

``
cd Meli-Order-Book/order-book
``

**3 - Rode o seguinte comando:**  

``
docker-compose up -d --build
``

**4 - Importe a seguinte Postman Collection:**  

[Order-Book-Postman-Collection](https://github.com/LuanPSantos/Meli-Order-Book/blob/main/postman/Order%20Book.postman_collection.json)

## Casos de Uso

### Get Order Book (Buscar livro de ofertas)
**Input**:  
None  

**Output**:  
Bids e Asks que estão em trade no momento da consulta

### Get Wallet By ID (Buscar carteira pelo ID)
**Input**:  
ID da Carteira

**Output**:  
Valores atuais na carteira e valores atuais em trade pertencentes a carteira (quantiade em dinheiro e quantiade de vibranium)

### Place a Sell Order (Realizar uma oferta de venda)
**Input**:  
Quantiade à venda e valor pedido por cada unidade de vibranium

**Output**:  
None

### Place a Buy Order (Realizar uma oferta de compra)
**Input**:  
Quantiade que se quer comprar e o valor máximo desejada que se pagar por cada unidade de vibranium

**Output**:  
None

### Get Trade History (Buscar hsitórico de transações)
**Input**:  
número da página e tamanho da página

**Output**:  
Lista com as trasações realizadas

### Cancel a Sell Order (Cancelar uma orfeta de venda)
**Input**:  
ID da oferta

**Output**:  
None

### Cancel a Buy Order (Cancelar uma orfeta de compra)
**Input**:  
ID da oferta

**Output**:  
None
