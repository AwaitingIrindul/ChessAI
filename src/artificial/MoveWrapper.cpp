//
// Created by Mathieu Regnard on 19/05/2017.
//

#include "MoveWrapper.h"

const Move &MoveWrapper::getMove() const {
    return move;
}

void MoveWrapper::setMove(const Move &move) {
    MoveWrapper::move = move;
}

MoveWrapper& MoveWrapper::toReference(MoveWrapper* pointer) {
    return *pointer;
}