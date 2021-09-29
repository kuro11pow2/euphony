//
// Created by kuro1 on 21-09-29.
//

#include "UTF16Charset.h"
#include <sstream>
#include <iomanip>
#include <iostream>

using namespace Euphony;

HexVector UTF16Charset::encode(std::string src) {
    // HexVector result(src.size());
    HexVector result{"ac00"};
    return result;
}

std::string UTF16Charset::decode(const HexVector &src) {
    // std::string result;
    // std::string stringSrc = src.toString();
    std::string result = "가";
    return result;
}
