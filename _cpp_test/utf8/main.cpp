
#include "HexVector.h"
#include "UTF8Charset.h"
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
        {"a", "61"},
        {"b", "62"},
        {"가", "eab080"},
        {"각", "eab081"},
        {"j", "6a"},
        {"나", "eb8298"},
        {"홍길동", "ed998deab8b8eb8f99"},
        {"@XYZ", "4058595a"},
        {".com", "2e636f6d"},
        {"서울특별시", "ec849cec9ab8ed8ab9ebb384ec8b9c"},
        {"010-1234-5678", "3031302d313233342d35363738"},
        {"36.5℃", "33362e35e28483"},
        {"유포니", "ec9ca0ed8faceb8b88"},
        {"euphony", "657570686f6e79"}};

    int n_testcase(0), n_succeed(0);
    for (int i = 0; i < testcases.size(); i++)
    {
        pair<string, string> &testcase = testcases[i];
        // bool encode_result = encode_test(testcase.first, testcase.second, true);
        bool decode_result = decode_test(testcase.second, testcase.first, true);
        // if (encode_result)
            // n_succeed++;
        if (decode_result)
            n_succeed++;
    }
    cout << "전체 테스트 케이스: " << testcases.size() * 2 << "\n실패한 테스트 케이스: " << testcases.size() * 2 - n_succeed << "\n";
    return 0;
}

void hexvector_test() {
    Charset *charset = new UTF8Charset();
    HexVector hv {"abc"};
    cout << hv.toString();
    for (auto x : hv) {
        cout << std::hex << (int)x;
    }
}

bool encode_test(string input, string expected_output, bool print)
{
    bool ret = false;
    Charset *charset = new UTF8Charset();
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

    cout << "enc" << "\n";
    if (print)
        cout << "원래 문자열:\t" << input << "\n기대 문자열:\t" << expected_output << "\n실제 문자열:\t" << actual_output << "\n\n";

    return ret;
}

bool decode_test(string input, string expected_output, bool print)
{
    bool ret = false;
    Charset *charset = new UTF8Charset();
    HexVector hv{input};
    string actual_output = charset->decode(hv);

    cout << "dec" << "\n";
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
