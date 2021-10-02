# CMake 프로그램의 최소 버전
cmake_minimum_required(VERSION 3.11)

# 프로젝트 정보
project(
    UTF8TESTCODE
    VERSION 0.1
    DESCRIPTION "utf8 test driver"
    LANGUAGES CXX
)

add_executable(
    main 
    main.cpp
    HexVector.cpp
    Charset.h
    UTF8Charset.cpp
    print.cpp
)