cd /usr/src/app/
echo $1
javac -d bin -cp "lib/*:src/" src/*.java
java @src/args.argfile ShippingApiHandler