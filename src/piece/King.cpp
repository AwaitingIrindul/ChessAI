#include "King.hpp"
#include "Rook.hpp"
#include <iostream>

namespace chesspp
{
    namespace piece
    {
        static auto KingRegistration = board::Board::registerPieceClass
        (
            "King",
            [](board::Board &b, board::Board::Position_t const &p, board::Board::Suit const &s)
            -> board::Board::Pieces_t::value_type
            {
                return board::Board::Pieces_t::value_type(new King(b, p, s, "King"));
            }
        );

        King::King(board::Board &b, Position_t const &pos_, Suit_t const &s_, Class_t const &pc)
        : Piece{b, pos_, s_, pc}
        {
        }

        void King::calcTrajectory()
        {
            //Kings can move one space in all eight directions
            using Dir = util::Direction;
            for(Dir d : {Dir::North
                        ,Dir::NorthEast
                        ,Dir::East
                        ,Dir::SouthEast
                        ,Dir::South
                        ,Dir::SouthWest
                        ,Dir::West
                        ,Dir::NorthWest})
            {
                Position_t t = Position_t(pos).move(d);


                if(moves == 0)
                    castling = true;
                //Added check if capturing (King cannot move to a capturing tile)
                auto it = std::find_if(board.pieceCapturings().begin(),
                                       board.pieceCapturings().end(),
                                       [&](board::Board::Movements_t::value_type const &m){
                                          return (*(m.first))->suit != this->suit &&
                                            (m.second == t);
                                       }

                );

                //If no piece can capture this position, we can move to it
                if(it == board.pieceCapturings().end()){
                    addTrajectory(t);
                    addCapturing(t);
                 //   castling = false; //Can't castle when there is a possibility of capture on the way
                }



            }

            if(castling){

                for (auto piece = board.begin(); piece != board.end(); piece++) {
                    if((*piece)->classname() == "Rook" && (*piece)->suit == this->suit){
                        Rook r= dynamic_cast<Rook&>(**piece);
                        if(r.castling){
                            int x = r.pos.x;
                            if( x - pos.x < 0) {

                                bool ok = true;
                                for (int i = 1; i < 3; ++i) {
                                    Dir dir = Dir::West;
                                    Position_t toCheck = Position_t(pos).move(dir, i);

                                    bool occupied = board.occupied(toCheck);
                                    auto check = std::find_if(board.pieceCapturings().begin(),
                                                              board.pieceCapturings().end(),
                                                              [&](board::Board::Movements_t::value_type const &m){
                                                                  return (*(m.first))->suit != this->suit &&
                                                                         (m.second == toCheck);
                                                              }

                                    );

                                    if(check != board.pieceCapturings().end() || occupied){
                                        ok = false;
                                    }

                                }

                                if(ok){
                                    Dir dir = Dir::West;
                                    Position_t toCheck = Position_t(pos).move(dir, 3);

                                    bool occupied = board.occupied(toCheck);

                                    if(occupied)
                                        ok = false;
                                }


                                if(ok) {
                                    Dir dir = Dir::West;
                                    Position_t  toGo = Position_t(pos).move(dir, 2);
                                    addTrajectory(toGo);
                                }
                            }
                        }
                    }
                }
            }
        }

        void King::tick(const Piece::Position_t &m) {
            using Dir = util::Direction;
            if(m == pos){
               if(pos.x == 2 && moves == 1){
                   for (auto piece = board.begin(); piece != board.end(); piece++) {
                       if ((*piece)->classname() == "Rook" && (*piece)->suit == this->suit) {

                           Dir dir = Dir::East;
                           Position_t  toGo = Position_t(pos).move(dir, 1);

                           (*piece)->move(toGo);
                            board.update(toGo, suit);
                       }
                   }
               }


            }
            if(moves == 1 && m != pos)
            { //moved just happened, castling no longer allowed
                castling = false;
            }


        }

        std::string King::classname() {
            return "King";
        }
    }
}
