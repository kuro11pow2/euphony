#ifndef EUPHONY_HEXVECTOR_H
#define EUPHONY_HEXVECTOR_H

#include <cstdint>
#include <vector>
#include <stdint.h>
#include <string>

namespace Euphony
{
    class HexVector
    {
    public:
        HexVector(int size);
        HexVector(const HexVector &copy);
        HexVector(const std::string &hexString);
        HexVector(const std::vector<u_int8_t> &hexVectorCopy);
        ~HexVector();

        void pushBack(u_int8_t hexByte);
        void popBack();
        std::string toString() const;
        const std::vector<u_int8_t> &getHexSource() const;
        void setHexSource(const std::vector<u_int8_t> &hexSource);
        int getSize() const;
        void clear();

        typedef std::vector<u_int8_t> HexSource;
        typedef HexSource::iterator iterator;
        typedef const HexSource::const_iterator const_iterator;
        const_iterator begin() const;
        const_iterator end() const;

        // std::__wrap_iter<std::vector<unsigned char, std::allocator<unsigned char>>::const_pointer>
        // begin() const;
        // std::__wrap_iter<std::vector<unsigned char, std::allocator<unsigned char>>::const_pointer>
        // end() const;

    private:
        HexSource hexSource;
    };
}

#endif //EUPHONY_HEXVECTOR_H
