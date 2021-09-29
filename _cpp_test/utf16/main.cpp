#include "HexVector.h"
#include "UTF16Charset.h"
#include <bits/stdc++.h>

using namespace std;
using namespace Euphony;

bool encode_test(string input, string expected_output, bool print = false);
bool decode_test(string input, string expected_output, bool print = false);
void print_bin_hex(string utf8string);

int main()
{
    vector<pair<string, string>> testcases{
        {"a", "61"},
        {"b", "62"},
        {"가", "ac00"},
        {"각", "ac01"}};

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
    HexVector hv = charset->encode(input);
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

void print_bin_hex(string utf8string)
{
    ios_base::fmtflags f(cout.flags());
    cout << "original utf8string string \"" << utf8string << "\" " << utf8string.size() << " code units:\n";
    for (char c : utf8string)
        cout << bitset<8>(c) << '\n';
    cout << "\n";

    // the UTF-8 / UTF-16 standard conversion facet
    wstring_convert<codecvt_utf8_utf16<char16_t>, char16_t> cv8to16;
    u16string utf16 = cv8to16.from_bytes(utf8string.data());
    cout << "UTF16 conversion produced " << utf16.size() << " code units:\n";
    for (char16_t c : utf16)
        cout << bitset<16>(c) << "\t" << hex << showbase << c << '\n';
    cout << "\n";

    // the UTF-8 / UTF-32 standard conversion facet
    wstring_convert<codecvt_utf8<char32_t>, char32_t> cv8to32;
    u32string utf32 = cv8to32.from_bytes(utf8string);
    cout << "UTF32 conversion produced " << dec << utf32.size() << " code units:\n";
    for (char32_t c : utf32)
        cout << bitset<32>(c) << "\t" << hex << showbase << c << '\n';
    cout.flags(f);
    cout << "\n\n";
}