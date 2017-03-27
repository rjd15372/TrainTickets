cmake_minimum_required(VERSION 3.3)
project(traintickets)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    src/traintickets.c
    src/ticket_protocol.c
    src/ticket_protocol.h)

add_executable(traintickets ${SOURCE_FILES})

include_directories("/usr/local/Cellar/pebble-sdk/3.4/Pebble/aplite/include/")
