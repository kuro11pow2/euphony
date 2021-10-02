#include "UTF8Charset.h"

#include <iostream>
#include <sstream>
#include <iomanip>
#include <codecvt>
#include <bitset>
#include <string>

using namespace Euphony;

const unsigned int BIT_COUNT_IN_HEX_NUM = 4;
const unsigned int BYTE_COUNT = 2;

HexVector UTF8Charset::encode(std::string src){
    HexVector result = HexVector(src.size());

    for(int i = 0;i < src.size();i++)
        result.pushBack(src[i]);

    return result;
}

std::string UTF8Charset::decode(const HexVector &src){
    std::vector<u_int8_t> hexSource = src.getHexSource();
    std::string tmp = src.toString();

    std::istringstream iss(tmp);
    std::cout << iss.str() << "\n";
    
    int x;
    iss >> std::hex >> x;
    std::ostringstream oss;
    oss << x;
    std::string s(oss.str());
    return s;

    /*

    입력: 16진수로 적혀진 정수의 배열 

    가각
    e a b 0 8 0 e a b 0 8 1
    11101010 10110000 10000000 11101010 10110000 10000001

    출력: utf8string 

    */
}
