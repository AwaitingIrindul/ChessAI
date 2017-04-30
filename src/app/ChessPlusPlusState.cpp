#include "ChessPlusPlusState.hpp"

#include "StartMenuState.hpp"
#include "util/Utilities.hpp"

#include <iostream>
#include <algorithm>

namespace chesspp
{
    namespace app
    {
        ChessPlusPlusState::ChessPlusPlusState(Application &app_, sf::RenderWindow &disp)
        : AppState(disp)                    //can't use {}
        , app(app_)                         //can't use {}
        , res_config(app.resourcesConfig()) //can't use {}
        , board_config{res_config}
        , graphics{display, res_config, board_config}
        , board{board_config}
        , players{util::KeyIter<config::BoardConfig::Textures_t>
                               (board_config.texturePaths().cbegin()),
                  util::KeyIter<config::BoardConfig::Textures_t>
                               (board_config.texturePaths().cend())}
        , turn{players.find(board_config.metadata("first turn"))}
        {
            if(turn == players.end())
            {
                turn = players.begin();
            }
        }

        void ChessPlusPlusState::nextTurn()
        {
            if(++turn == players.end())
            {
                turn = players.begin();
            }
        }

        board::Board::Pieces_t::iterator ChessPlusPlusState::find(board::Board::Position_t const &pos) const
        {
            return std::find_if(board.begin(), board.end(),
            [&](std::unique_ptr<piece::Piece> const &up) -> bool
            {
                return up->pos == pos;
            });

             // Return an iterator to the piece on that position
        }

        void ChessPlusPlusState::onRender()
        {
            graphics.drawBoard(board);
            if(selected != board.end())
            {
                graphics.drawTrajectory(**selected);
            }
            if(board.valid(p))
            {
                auto piece = find(p);
                if(piece != board.end())
                {
                    graphics.drawTrajectory(**piece, (*piece)->suit != *turn);
                }
            }
        }

        void ChessPlusPlusState::onKeyPressed(sf::Keyboard::Key key, bool /*alt*/, bool /*control*/, bool /*shift*/, bool /*system*/)
        {
            if(key == sf::Keyboard::Escape)
            { //Exit to menu screen
                return app.changeState<StartMenuState>(std::ref(app), std::ref(display));
            }

            //TODO : Ajouter une touche ici pour la gestion des coups spéciaux, genre roque ect..
        }
        void ChessPlusPlusState::onMouseMoved(int x, int y)
        {
            p.x = static_cast<board::Board::Position_t::value_type>(x/board.config.cellWidth());
            p.y = static_cast<board::Board::Position_t::value_type>(y/board.config.cellHeight());
            //TODO : Refactor pour mettre ça dans onLButtonPressed : plus propre je pense, a voir
        }
        void ChessPlusPlusState::onLButtonPressed(int x, int y)
        {
        }
        void ChessPlusPlusState::onLButtonReleased(int x, int y)
        {
            // TODO : Gestion des mouvements ici
            if(!board.valid(p)) return;
            if(selected == board.end()) //If no piece was selected, we select the piece at the current clicked position
            {
                selected = find(p); //doesn't matter if board.end(), selected won't change then
                if(selected != board.end() && (*selected)->suit != *turn)
                {
                    selected = board.end(); //can't select enemy pieces
                }
            }
            else //If a piece was selected, we can apply the movement
            {
                if(find(p) == board.end() || (*find(p))->suit != (*selected)->suit)[&] //If we are on an empty grid or on enemy piece.
                {
                    {
                        auto it = std::find_if(board.pieceCapturings().begin(),
                                               board.pieceCapturings().end(),
                                               [&](board::Board::Movements_t::value_type const &m)
                                               {
                                                   return m.first == selected && m.second == p;
                                               });
                        //it contain the piece to capture

                        if(it != board.pieceCapturings().end()) //If we have a piece to capture
                        {
                            for(auto jt = board.pieceCapturables().begin(); jt != board.pieceCapturables().end(); ++jt)
                            {
                                //Jt contains the capturable piece
                                if(jt->second == p)
                                {
                                    //If the capturable piece is at the position we are going to land
                                    if(board.capture(selected, it, jt, *turn)) // We can capture the piece
                                    {
                                        nextTurn();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    { //Creation of second scope to allow reusage of it


                        auto it = std::find_if(board.pieceTrajectories().begin(),
                                               board.pieceTrajectories().end(),
                                               [&](board::Board::Movements_t::value_type const &m)
                                               {
                                                   return m.first == selected && m.second == p;
                                               });
                        //it contain the tile we are landing on
                        if(it != board.pieceTrajectories().end()) //If the tile exists, i.e the movement is possible
                        {
                            if(board.move(selected, it, *turn)) //We move
                            {
                                nextTurn();
                            }
                        }
                    }
                }();
                selected = board.end(); //deselect
            }
        }
    }
}
