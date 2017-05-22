//
// Created by Mathieu Regnard on 19/05/2017.
//

#ifndef CHESSPLUSPLUS_PLAYER_H
#define CHESSPLUSPLUS_PLAYER_H


#include <board/Board.hpp>

namespace chesspp
{
    namespace board{
        class Board;
    }
    namespace player
    {



        class Player {
        private:
            chesspp::board::Board &board;
            using Move = chesspp::board::Board::Movements_t;
        public:
            double negamax(std::pair<board::Board::Pieces_t::const_iterator, board::Board::Position_t> & move, const int depth, double alpha, double beta);
            double evaluate();
            void executeMove();
            void undoMove();

            Player(board::Board &board);

            const board::Board::Movements_t & getAvailableMoves();
        };
    }
}


#endif //CHESSPLUSPLUS_PLAYER_H
