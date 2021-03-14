# KeyValueStore

In this project we will be creating a simple version of a distributed, fault-tolerant, [Key-Value (KV)](https://en.wikipedia.org/wiki/Key%E2%80%93value_database)
database (or store), with a few tweaks. From Wikipedia: a key–value database, or key–value store, is a
data storage paradigm designed for storing, retrieving, and managing associative arrays, and a data
structure more commonly known today as a dictionary or hash table. Dictionaries contain a collection of
objects, or records, which in turn have many different fields within them, each containing data. These
records are stored and retrieved using a key that uniquely identifies the record, and is used to find the
data within the database. In our case, we will be using a [Trie](https://en.wikipedia.org/wiki/Trie) instead of a hash table for storing the
keys.