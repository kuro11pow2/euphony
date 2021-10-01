# CMake 프로그램의 최소 버전
cmake_minimum_required(VERSION 3.11)

# 프로젝트 정보
project(
    UTF16TESTCODE
    VERSION 0.1
    DESCRIPTION "utf16 test driver"
    LANGUAGES CXX
)

add_executable(
    main 
    main.cpp
    HexVector.cpp
    Charset.h
    UTF16Charset.cpp
    print.cpp
)