#include "King.hpp"
#include "Rook.hpp"

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
                    castling = false; //Can't castle when there is a possibility of capture on the way
                }


                if(castling){



                    for (auto piece = board.begin(); piece != board.end(); piece++) {
                        if((*piece)->classname() == "Rook" && (*piece)->suit != this->suit){
                            Rook r= dynamic_cast<Rook&>(**piece);
                            if(r.castling){
                                int x = r.pos.x;
                                if( x - pos.x > 0){
                                    for (int i = 1; i <= 3; ++i) {
                                        Dir dir = Dir::West;
                                        Position_t toCheck = Position_t(pos).move(dir, i);
                                        // TODO 19/04/2017 : Check if nothing, if non echec
                                    }


                                } else if( x - pos.x < 0 ){
                                    smallCastle = false;
                                }
                            }
                        }
                    }
                }

            }
        }

        void King::tick(const Piece::Position_t &m) {
            if(moves == 1 && m != pos)
            { //moved just happened, castling no longer allowed
                castling = false;
                // TODO 19/04/2017 : If King moved from two, castling is done here
            }

        }

        std::string King::classname() {
            return "King";
        }
    }
}
