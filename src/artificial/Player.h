//
// Created by Mathieu Regnard on 19/05/2017.
//

#ifndef CHESSPLUSPLUS_PLAYER_H
#define CHESSPLUSPLUS_PLAYER_H


#include "MoveWrapper.h"

namespace chesspp
{
    namespace player
    {
        class Move;

        class Player {
        public:
            double negamax( MoveWrapper& wrapper, const int depth, double alpha, double beta);
            double evaluate();
            std::vector<Move> getAvailableMoves();
        public:

            void executeMove();

            void undoMove();
        };
    }
}


#endif //CHESSPLUSPLUS_PLAYER_H
