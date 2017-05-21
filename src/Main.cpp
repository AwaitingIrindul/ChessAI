#include "app/Application.hpp"
#include "app/StartMenuState.hpp"

#include <iostream>
#include <artificial/NeuralNetwork.h>

/**
 * \brief
 * Entry point of the application.
 * 
 * Sets up the application window and then passes control to chesspp::app::StartMenuState
 * 
 * \param nargs argument count, not used.
 * \param args arguments, not used.
 */
int main(int nargs, char const *const *args)
{
#ifdef CHESSPP_REDIRECT_OUTPUT
    chesspp::enableRedirection();
#endif

    try
    {
        sf::RenderWindow disp
        {
            sf::VideoMode(640, 640),
            "Chess AI",
            sf::Style::Close
        };
        chesspp::app::Application app {disp};
        app.changeState<chesspp::app::StartMenuState>(std::ref(app), std::ref(disp));
        return app.execute();
    }
    catch(std::exception &e)
    {
        std::clog << typeid(e).name() << " caught in main: " << e.what() << std::endl;
        return EXIT_FAILURE;
    }
    /*
    NeuralNetwork nn(2, 2);
    nn.addHiddenLayer(2);
    auto input = std::vector<double>{0.7, 0.5};
    auto out =  nn.feedforward(input);
    std::cout<<"Out"<<std::endl;
    for (int i = 0; i < out.size(); ++i) {
        std::cout<<out[i]<<std::endl;
    }
    */
}
