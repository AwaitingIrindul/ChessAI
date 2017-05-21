//
// Created by Mathieu Regnard on 19/05/2017.
//

#ifndef CHESSPLUSPLUS_PLAYER_H
#define CHESSPLUSPLUS_PLAYER_H


#include "MoveWrapper.h"

namespace chesspp
{
    namespace board{
        class Board;
    }
    namespace player
    {
        friend class chesspp::board::Board;


        class Player {
        private:
            chesspp::board::Board &board;
            
        public:
            double negamax( MoveWrapper& wrapper, const int depth, double alpha, double beta);
            double evaluate();
            std::vector<Move> getAvailableMoves();
            void executeMove();
            void undoMove();

            Player(board::Board &board);
        };
    }
}


#endif //CHESSPLUSPLUS_PLAYER_H
