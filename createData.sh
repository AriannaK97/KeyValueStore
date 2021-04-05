#!/bin/bash
#call with 10 args

get_random_string(){
    FLOOR=1
    let strLength=$RANDOM+$FLOOR;
    string=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w "$l" | head -n 1)
    echo "$string"
}

get_random_int(){
  int=0
  int=$RANDOM
  echo "$int"
}

get_random_float(){
  first=$(( $RANDOM % 100))
  second=$(( $RANDOM % 999))
  echo "$first.$second"
}

get_available_keys(){
  RANGE=$(< "$keyFile" wc -l) #number of lines is the range RANDOM can act
  file="somefileondisk"
  linesArray=`cat $keyFile`
#  for line in $linesArray; do
#          echo "$line"
#  done
  echo linesArray;
}

getUniqueKeyInLevel(){
    linesArray=$(get_available_keys)

    key = $linesArray[0]
    while [ "$key"  "$linesArray" ]
    do
      key=$($RANDOM % ${#linesArray[@]})

      done
}

get_random_key(){
  if [ "$curM" -gt 0 ]; then
    FLOOR=0;
    RANGE=$(< "$keyFile" wc -l) #number of lines is the range RANDOM can act
    picker=-1
    chanceToNest=$(( $RANDOM % 4))

    while [ "$picker" -le $FLOOR ]
    do
      picker=$((($RANDOM % ($RANGE)+1)))
      #let "picker = $picker % $RANGE"  # Scales $number down within $RANGE.
    done




    if [ $chanceToNest -eq 0 ]; then
      curM=$(($curM - 1))
      curD=$(( $RANDOM % d))
      for((x=0; x < "$curD"; x++)){
        if [ $x -eq 0 ]; then
          val=$(get_random_key);
        else
          val="$val; $(get_random_key)"
        fi
      }
      echo "\"${key[0]}\" : { $val }"
    elif [ "${key[1]}" = "int" ]; then
      echo "\"${key[0]}\" : \"$(get_random_int)\""
    elif [ "${key[1]}" = "string" ]; then
      echo "\"${key[0]}\" : \"$(get_random_string)\""
    elif [ "${key[1]}" = "float" ]; then
      echo "\"${key[0]}\" : \"$(get_random_float)\""
    fi
  fi
}

create_new_record(){
  for((i=0; i < "$n"; i++)){
    id="person$i"
    curM=$m
    for((j=0; j < "$d"; j++)){
      curD=$d
      if [ $j -eq 0 ]; then
        kv=$(get_random_key);
      else
        kv="$kv; $(get_random_key)"
      fi
    }
    echo -e "\"$id\" : { $kv }" >> ./"$filename"
  }
}

create_new_file(){
    while true;
    do
      filename="input"
      if [ ! -f "$filename" ]; then
          break
      else
        echo "File with name $filename already exists"
        exit
      fi
    done
    create_new_record
}

echo "All args are = $*";
echo "Number of Parameters = $#"

_k=$1;
keyFile=$2;
_n=$3;
n=$4; #number of lines generated
_d=$5;
d=$6; #maximum level of nesting
_m=$7;
m=$8; #maximum number of keys inside each value
_l=$9;
l=${10}; #maximum length of a string value

#checking input numbers
isNum='^[0-9]+$'
if ! [[ $n =~ $isNum ]] || ! [[ $d =~ $isNum ]] || ! [[ $m =~ $isNum ]] || ! [[ $l =~ $isNum ]] ;
then
    >&2 echo "Error! Wrong Parameters, better luck next time"
    exit 1
fi

echo "Creating input file with $n lines"
create_new_file