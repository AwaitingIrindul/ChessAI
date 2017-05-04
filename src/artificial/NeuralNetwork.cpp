//
// Created by Mathieu Regnard on 04/05/2017.
//

#include <cstdlib>
#include <random>
#include <iostream>
#include "NeuralNetwork.h"

NeuralNetwork::NeuralNetwork(int nbInput, int nbOutput) : layers(2), alpha(0.1), totalError(0), timestep(0) {

    layers[0] = std::vector<double>((unsigned long) nbInput);
    layers[1] = std::vector<double>((unsigned long) nbOutput);

    initMatrices();
}

void NeuralNetwork::initMatrices() {
    initWeights();
    initOtherMatrices();
}

void NeuralNetwork::initWeights() {
    std::random_device rd;  //Will be used to obtain a seed for the random number engine
    std::mt19937 gen(rd()); //Standard mersenne_twister_engine seeded with rd()
    std::uniform_real_distribution<> dis(-1, 1);

    weights = std::vector<std::vector<std::vector<double>>>(layers.size()-1);
    for (int i = 0; i < layers.size() - 1; ++i) {

        weights[i] = std::vector<std::vector<double>>(layers[i+1].size());

        for (int j = 0; j < weights[i].size(); ++j) {
            weights[i][j] = std::vector<double>(layers[i].size());
            for (int k = 0; k < weights[i][j].size(); ++k) {
                weights[i][j][k] = dis(gen); //Proper C++11 Way of generating random number
                std::cout<<weights[i][j][k]<<std::endl;
            }
        }
        std::cout<<std::endl;
    }

}

void NeuralNetwork::initOtherMatrices() {
    deltas = std::vector<std::vector<double>>(weights.size());
    weigthsUpdated = std::vector<std::vector<double>>(weights.size());
}

void NeuralNetwork::addHiddenLayer(int nbHidden) {
    auto matrix = std::vector<double>((unsigned long) nbHidden);
    layers.insert(layers.end()-1, matrix);
    initMatrices();
}

std::vector<double> NeuralNetwork::feedforward(std::vector<double> &input) {
    layers[0] = input;
    for (int i = 0; i < weights.size(); ++i) {
        layers[i+1] = matrixMult(layers[i], weights[i]);
        //Apply sigmoid
    }

    //Maybe log some stuff ?
    return layers[layers.size()-1];
}

std::vector<double> NeuralNetwork::matrixMult(std::vector<double> &first, std::vector<std::vector<double>> &second){
    auto output = std::vector<double>(second.size());
    for (int i = 0; i < output.size(); ++i) {
        output[i] = 0;
        for (int j = 0; j < second[i].size(); ++j) {
            output[i] += first[j] * second[i][j];
        }
    }

    return output;
}
