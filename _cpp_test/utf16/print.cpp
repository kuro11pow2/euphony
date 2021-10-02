
#include "print.h"

using namespace std;

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