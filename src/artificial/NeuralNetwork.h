//
// Created by Mathieu Regnard on 04/05/2017.
//

#ifndef CHESSPLUSPLUS_NEURALNETWORK_H
#define CHESSPLUSPLUS_NEURALNETWORK_H


#include <vector>

class NeuralNetwork {

    private:
        std::vector<std::vector<double>> layers;
        std::vector<std::vector<double>> deltas;
        std::vector<std::vector<std::vector<double>>> weights;
        std::vector<std::vector<double>> weigthsUpdated;
        int timestep;
        double totalError;
        double alpha;
    public:
    NeuralNetwork(int , int);

    void initMatrices();

    void initWeights();

    void initOtherMatrices();

    void addHiddenLayer(int nbHidden);

    std::vector<double> feedforward(std::vector<double> &input);

    static std::vector<double> matrixMult(std::vector<double> &first, std::vector<std::vector<double>> &second);
};


#endif //CHESSPLUSPLUS_NEURALNETWORK_H
