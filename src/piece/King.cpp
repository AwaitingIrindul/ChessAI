#include "King.hpp"

#include <initializer_list>

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
                }


            }
        }

        void King::tick(const Piece::Position_t &m) {
            if(moves == 1 && m != pos)
            { //moved just happened, castling no longer allowed
                //castling = false;

            }
            // TODO 19/04/2017 : ajouter attribut castling.
        }
    }
}
