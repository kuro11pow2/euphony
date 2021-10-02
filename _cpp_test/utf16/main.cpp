
#include "HexVector.h"
#include "UTF16Charset.h"
#include <bits/stdc++.h>

using namespace std;
using namespace Euphony;

bool encode_test(string input, string expected_output, bool print = false);
bool decode_test(string input, string expected_output, bool print = false);
void print_bin_hex(string utf8string);
void hexvector_test();

int main()
{
    vector<pair<string, string>> testcases{
        {"a", "0061"},
        {"b", "0062"},
        {"가", "ac00"},
        {"각", "ac01"},
        {"j", "006a"},
        {"나", "b098"},
        {"홍길동", "d64dae38b3d9"},
        {"@XYZ", "004000580059005a"},
        {".com", "002e0063006f006d"},
        {"서울특별시", "c11cc6b8d2b9bcc4c2dc"},
        {"010-1234-5678", "003000310030002d0031003200330034002d0035003600370038"},
        {"36.5℃", "00330036002e00352103"}};

    int n_testcase(0), n_succeed(0);
    for (int i = 0; i < testcases.size(); i++)
    {
        pair<string, string> &testcase = testcases[i];
        bool encode_result = encode_test(testcase.first, testcase.second, true);
        bool decode_result = decode_test(testcase.second, testcase.first, true);
        if (encode_result)
            n_succeed++;
        if (decode_result)
            n_succeed++;
    }
    cout << "전체 테스트 케이스: " << testcases.size() * 2 << "\n실패한 테스트 케이스: " << testcases.size() * 2 - n_succeed << "\n";
    return 0;
}

void hexvector_test() {
    Charset *charset = new UTF16Charset();
    HexVector hv {"abc"};
    cout << hv.toString();
    for (auto x : hv) {
        cout << std::hex << (int)x;
    }
}

bool encode_test(string input, string expected_output, bool print)
{
    bool ret = false;
    Charset *charset = new UTF16Charset();
    HexVector hv = charset->encode(input);
    string actual_output = hv.toString();
    if (expected_output == actual_output)
    {
        ret = true;
    }
    else
    {
        if (print)
            cout << "테스트 실패\n";
        ret = false;
    }

    if (print)
        cout << "원래 문자열:\t" << input << "\n기대 문자열:\t" << expected_output << "\n실제 문자열:\t" << actual_output << "\n\n";

    return ret;
}

bool decode_test(string input, string expected_output, bool print)
{
    bool ret = false;
    Charset *charset = new UTF16Charset();
    HexVector hv{input};
    string actual_output = charset->decode(hv);

    if (print)
        cout << "원래 문자열:\t" << input << "\n기대 문자열:\t" << expected_output << "\n실제 문자열:\t" << actual_output << "\n\n";
    if (expected_output == actual_output)
    {
        ret = true;
    }
    else
    {
        if (print)
            cout << "테스트 실패\n";
        ret = false;
    }
    return ret;
}
