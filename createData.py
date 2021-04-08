import sys
import getopt
import string
import random


class DataCreator:

    def __init__(self, keyfile, n, d, l, m):
        self.outfile = open("inputFile.txt", "a")
        self.keyfile = keyfile
        self.usedKeysListPerLevel = []
        self.keys = []
        self.n = n
        self.d = d
        self.l = l
        self.m = m
        self.curd = d

    """ Get all the available keys as specified in the file given at input """
    def get_available_keys(self):
        f = open(self.keyfile, "r")
        keys = f.readlines()
        f.close()
        return keys

    """ Check for key duplication """
    def is_key_duplicate(self, currentKey):
        for key in self.usedKeysListPerLevel:
            if key is currentKey:
                return True
        return False

    """ Generate a matching value for the type specified for the selected key """
    def generate_key_matching_type_value(self, key):
        if "string" in key:
            return ''.join(random.choices(string.ascii_uppercase + string.digits, k=self.l))
        if "int" in key:
            return random.randint(0, 1000000000)
        if "float" in key:
            return random.uniform(0, 1000000000)

    """ select a random key from the available ones given at input """
    def get_random_key(self):
        rand = random.randint(0, len(self.keys)-1)
        key = self.keys[rand]
        return key

    """ Generate one record """
    def generate_record(self, nesting_level, record=""):

        key = self.get_random_key()
        num_of_used_keys = 0
        while self.is_key_duplicate((key, nesting_level)):
            if num_of_used_keys == len(self.usedKeysListPerLevel):
                return ""
            num_of_used_keys += 1
            key = self.get_random_key()
        self.usedKeysListPerLevel = (key, nesting_level)

        chance_to_nest = random.randint(0, 4)
        if chance_to_nest == 0:
            self.curd -= 1
            curm = random.randint(0, self.m)        #number of keys inside the current level
            for i in range(curm):
                if i == 0:
                    record = self.generate_record(curm, record)
                else:
                    record = record + "; " + self.generate_record(curm, record)
                self.curd += 1
            key_split = key.split()
            return "\"" + str(key_split[0]) + "\" : { " + record + " }"
        else:
            key_split = key.split()
            if "string" in key:
                return "\"" + str(key_split[0]) + "\":\"" + (''.join(random.sample(string.ascii_uppercase + string.digits, k=self.l))) +  "\""
            if "int" in key:
                return "\"" + str(key_split[0]) + "\": " + str(random.randint(0, 10000))
            if "float" in key:
                return "\"" + str(key_split[0]) + "\": " +  str(random.uniform(0, 10000))
            #return "\"" + str(key_split[0]) + "\":\"" + str(self.generate_key_matching_type_value(key)) + "\""

    """ Generate the total number of records """
    def generator(self):
        self.keys = self.get_available_keys()
        for line in range(self.n):
            self.usedKeysListPerLevel = []
            self.curM = self.m
            keysPerLine = random.randint(0, self.m)
            if keysPerLine == 0:
                record = "\"person" + str(line) + "\" : { }\n"
            else:
                record = "\"person" + str(line) + "\" : { "
                for key in range(keysPerLine):
                    if key == 0:
                        record += str(self.generate_record(self.d))
                    else:
                        record += "; " + str(self.generate_record(self.d))
                record += " }\n"
            self.outfile.write(record)
        self.outfile.close()


""" Check input arguments for validity """
def parse_input_args(argv):
    try:
        opts, args = getopt.getopt(argv, "k:n:d:l:m:", ["k=", "n=", "d=", "l=", "m="])
    except getopt.GetoptError:
        print('createData.py -k <inputfile> -n <number of lines generated> -d <maximum level of nesting> '
              '-l <maximum number of keys inside each value> -m <maximum length of a string value>')
        sys.exit(2)

    for opt, arg in opts:
        if opt == '-k':
            keyfile = arg
        elif opt in ("-n", "--n"):
            n = int(arg)
        elif opt in ("-d", "--d"):
            d = int(arg)
        elif opt in ("-l", "--l"):
            l = int(arg)
        elif opt in ("-m", "--m"):
            m = int(arg)

    return keyfile, n, d, l, m


if __name__ == "__main__":
    keyfile, n, d, l, m = parse_input_args(sys.argv[1:])
    DataCreator = DataCreator(keyfile, n, d, l, m)
    DataCreator.generator()
