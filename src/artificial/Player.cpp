//
// Created by Mathieu Regnard on 19/05/2017.
//

#include <vector>
#include <iostream>
#include "Player.h"


namespace chesspp {
    namespace player {

        double Player::negamax(std::pair<board::Board::Pieces_t::const_iterator, board::Board::Position_t> & move, const int depth, double alpha, double beta) {
            if(depth == 0) { // TODO 19/05/2017 : Or game is finished
                return evaluate();
            }

            auto moves = getAvailableMoves();
            Move::iterator best = moves.begin();

            double score = 0;

            auto it = moves.begin();

            for(it; it != moves.end(); ++it){
                executeMove();
                score = -negamax(move, depth - 1, -beta, -alpha);
                undoMove();
                if(score > alpha){
                    alpha = score;
                    best = it;
                    if(alpha >= beta){
                        break;
                    }
                }
            }


            std::cout<<"Setting best move"<<std::endl;
            move = (*best);

            return alpha;
        }

        double Player::evaluate() {
            return 0;
        }


        void Player::executeMove() {

        }

        void Player::undoMove() {

        }

        Player::Player(board::Board &board) : board(board) {
            auto move = board.getTrajectories();
        }

        const board::Board::Movements_t & Player::getAvailableMoves() {
            return board.getTrajectories();
        }
    }
}


