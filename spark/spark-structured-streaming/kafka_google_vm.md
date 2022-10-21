# Resumen Levantar KAFKA en una VM para usar con Structured Streaming

### 1. Crear instancia VM

Una vez creada la instancia de VM con el siguiente tipo de máquina:

* e2-standard-4

Debemos hacer dos acciones para que podamos acceder desde nuestro PC a la nueva instancia

* Debemos añadir una etiqueta de red 'kafka' (Editar instancia)
* Crear una regla de firewall para permitir conexiones a esa etiqueta:
	* Etiquetas destino especificadas => kafka
	* Rangos ipv4 origen => 0.0.0.0/0
	* Puertos => TCP 9092

### 2. Instalar Java

```bash
sudo -i

apt-get update -y

apt-get install default-jre wget -y

java -version  --ESPERAR

```

### 3. Instalar Docker

```bash

apt-get install \
apt-transport-https \
ca-certificates \
curl \
gnupg-agent \
software-properties-common -y

curl -fsSL https://download.docker.com/linux/debian/gpg | sudo apt-key add -

add-apt-repository \
"deb [arch=amd64] https://download.docker.com/linux/debian \
$(lsb_release -cs) \
stable"

apt-get update -y


apt-get install docker-ce docker-ce-cli containerd.io -y


docker run hello-world

```
### 4. Descargar Kafka y configurar

**Realizar este punto cada vez que levantemos nuestra maquina, pues la IP Publica cambia** 

```bash
wget https://downloads.apache.org/kafka/3.3.1/kafka_2.12-3.3.1.tgz && tar -xvf kafka_2.12-3.3.1.tgz


cd kafka_2.12-3.3.1
vim config/server.properties

# descomentamos la linea advertised.listener y añadimos la IP publica
# advertised.listeners=PLAINTEXT://23.251.158.221:9092

#comprobamos que esta correctamente con este comando
cat config/server.properties | grep advertised

```

### 5. Lanzar kafka

```bash
bin/zookeeper-server-start.sh -daemon config/zookeeper.properties

bin/kafka-server-start.sh -daemon config/server.properties

```


### 6. Crear topics, consumer y producer

```bash

# crear topic
bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic test --partitions 1 --replication-factor 1 --create

# listar topics
bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# abrir un producer
bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic test

# abrir un consumer
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning


# abrir consumer que muestre la key de los mensajes
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test1 --property print.key=1

```

