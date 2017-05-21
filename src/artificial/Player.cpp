//
// Created by Mathieu Regnard on 19/05/2017.
//

#include <vector>
#include <iostream>
#include "Player.h"
#include "Move.h"

namespace chesspp {
    namespace player {
        double Player::negamax(MoveWrapper& wrapper, const int depth, double alpha, double beta) {
          /*  if(depth == 0) { // TODO 19/05/2017 : Or game is finished
                return evaluate();
            }

            std::vector<Move> moves = getAvailableMoves();
            MoveWrapper& nullRef = MoveWrapper::toReference(nullptr);
            double score = 0;
            Move best;
            for(auto move : moves){
                executeMove();
                score = -negamax(nullRef, depth - 1, -beta, -alpha);
                undoMove();
                if(score > alpha){
                    alpha = score;
                    best = move;
                    if(alpha >= beta){
                        break;
                    }
                }
            }


            if(&nullRef == &wrapper){
                std::cout<<"Setting best move"<<std::endl;
                wrapper.setMove(best);
            }

            return alpha;*/
            return 0;



        }

        double Player::evaluate() {
            return 0;
        }

        std::vector<Move> Player::getAvailableMoves() {
            return std::vector<Move, std::allocator<Move>>();
        }

        void Player::executeMove() {

        }

        void Player::undoMove() {

        }
    }
}


