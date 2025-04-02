#!/bin/bash

test() {
    local input_file="src/test/magik/$1"
    local expected_output="$2"

    printf "\e[33mRunning test for %s\e[0m\n" "$input_file"

    # Run the JAR and capture the output
    output=$(java -jar --add-exports java.base/jdk.internal.classfile.components=ALL-UNNAMED --enable-preview target/majestik*.jar "$input_file")

    # Compare the output with the expected output
    if [ "$output" = "$expected_output" ]; then
        printf "\e[32mTest passed for %s\e[0m\n" "$input_file"
    else
        printf "\e[31mTest failed for %s\e[0m\n" "$input_file"
        printf "\e[33mExpected: '%s'\e[0m\n" "$expected_output"
        printf "\e[33mGot: '%s'\e[0m\n" "$output"
        exit 1
    fi
}

# Read and execute test cases from configuration
while read -r case; do
    file=$(echo "$case" | jq -r '.file')
    description=$(echo "$case" | jq -r '.description')
    expected=$(echo "$case" | jq -r '.expected')

    printf "\e[36m%s\e[0m\n" "$description"
    test "$file" "$expected"
done < <(jq -c '.test_cases[]' src/test/magik/test_cases.json)

printf "\e[32mAll tests passed!\e[0m\n"
