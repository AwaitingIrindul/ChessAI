//
// Created by Mathieu Regnard on 19/05/2017.
//

#ifndef CHESSPLUSPLUS_MOVEWRAPPER_H
#define CHESSPLUSPLUS_MOVEWRAPPER_H


#include "Move.h"

class MoveWrapper {
private:
    Move move;
public:
    const Move &getMove() const;

    void setMove(const Move &move);

    static MoveWrapper &toReference(MoveWrapper *pointer);
};


#endif //CHESSPLUSPLUS_MOVEWRAPPER_H
