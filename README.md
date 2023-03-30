# back3

docker run -d -p 27017:27017 -v C:/Users/rc/Documents/work/mongodb1/mongo-data:/data/db --name mongodb mongo

docker run -d -p 27017:27017 --name mongodb mongo

docker rm -f mongodb
docker run -d -p 27017:27017 -v C:/Users/rc/Documents/work/mongodb2/mongo-data:/data/db --name mongodb mongo:5.0.15


docker run --link mongodb:mongo -p 8081:8081 -e ME_CONFIG_MONGODB_URL="mongodb://mongo:27017" mongo-express
