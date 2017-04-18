import numpy as np
from numpy import genfromtxt
import pandas as pd
import csv
import matplotlib.pyplot as plt
import pylab as pl

## Undersoke hvordan brukerne fordeler seg paa ulike communities

def user_distribution():
    # Load training data
    data1 = pd.read_csv("Results/Edinburgh/Edinburgh_businesses.tsv", sep="\t")
    data2 = pd.read_csv("Results/Edinburgh/Edinburgh_visits.csv", sep=",")
    business_ID = data1["businessId"]
    modularity = data1["modularity_class"]
    users = data2["userId"]
    n = len(users)
    visits = {}
    communities = {}
    max_mod_class = 0
    # create dictionary with business and modularity class
    for i in range(0,len(business_ID)):
        if communities.has_key(business_ID[i]):
            pass
        else:
            if modularity[i] != "null":
                communities[business_ID[i]] = int(modularity[i])
                max_mod_class = max(max_mod_class, int(modularity[i]))
    # Create dictionary with users
    for user in users:
        if visits.has_key(user):
            pass
        else:
            visits[user] = []
    # add a visit to the user dictionary
    for i in range(0,n):
        business = data2["businessId"][i]
        if business in communities:
            mod = communities[business]
            user = data2["userId"][i]
            visits[user].append(mod)

    # Find proportion of visit in each community
    proportions = {}
    for key in visits:
        if len(visits[key]) > 10:
            proportions[key] = []
            for i in range(0, max_mod_class+1):
                count = 0.0
                for j in visits[key]:
                    if j == i:
                        count += 1
                proportions[key].append(count/len(visits[key]))
    # Print the proportions 
    """
    for key in proportions:
        print key + "     " + str(proportions[key])
    for key in proportions:
        print max(proportions[key])
    """
    maximum = []
    for key in proportions:
        maximum.append(max(proportions[key]))
    plt.hist(maximum)
    plt.show()
    print "Mean = " + str(sum(maximum)/len(maximum))

user_distribution()