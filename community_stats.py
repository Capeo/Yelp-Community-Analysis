import numpy as np
import csv
import matplotlib.pyplot as plt
import operator
import sys

def plot(path):
    categoryCount = {}
    categoryModClassCount = {}
    modClassCategoryCount = {}
    modClasses = []

    with open(path, 'r') as csvfile:
        data3 = csv.reader(csvfile, delimiter='\t')
        count = 0
        for row in data3:
            s = row[8]
            s = s.split(",")
            s[0] = s[0][1:]
            s[len(s)-1] = s[len(s)-1][0:len(s[len(s)-1])-1]
            #print s
            modClass = row[9]
            if count > 0:
                if modClass != 'null':
                    modClass = int(modClass)
                    if not modClass in modClassCategoryCount:
                        modClassCategoryCount[modClass] = {}
                        modClasses.append(modClass)
                    for elem in s:
                        elem = elem.lstrip().rstrip()

                        if not elem in categoryCount:
                            categoryCount[elem] = 1
                        else:
                            categoryCount[elem] += 1

                        if not elem in modClassCategoryCount[modClass]:
                            modClassCategoryCount[modClass][elem] = 1
                        else:
                            modClassCategoryCount[modClass][elem] += 1

                        if not elem in categoryModClassCount:
                            categoryModClassCount[elem] = {}
                        if not modClass in categoryModClassCount[elem]:
                            categoryModClassCount[elem][modClass] = 1
                        else:
                            categoryModClassCount[elem][modClass] += 1
            count += 1

    # Sort categories by size and modularity classes by name
    sorted_categoryCount = sorted(categoryCount.items(), key=operator.itemgetter(1), reverse=True)
    modClasses = sorted(modClasses)

    print modClasses

    # Filter categories
    categories = []
    for category in sorted_categoryCount:
        if category[0] != "Restaurants" and category[1] > categoryCount["Restaurants"]/100:
            categories.append(category[0])

    # Plot bar chart
    color = iter(plt.cm.rainbow(np.linspace(0,1,len(modClasses))))
    bars = []
    bottom = [0 for i in range(len(categories))]
    width = 0.7
    x = np.arange(len(categories))
    for modClass in modClasses:
        data = []
        for category in categories:
            if modClass in categoryModClassCount[category]:
                if category == "Pizza":
                    print modClass, categoryModClassCount[category][modClass], categoryCount[category]
                data.append(float(categoryModClassCount[category][modClass])/float(categoryCount[category]))
            else:
                data.append(0)
        c = next(color)
        p = plt.bar(x, data, width, bottom=bottom, color=c)
        bars.append(p[0])
        for i in range(len(categories)):
            bottom[i] += data[i]
    plt.title("Percentage of category in each community")
    plt.ylabel("Percentage of category")
    plt.xlabel("Category")
    plt.xticks(x, categories, rotation="vertical")
    plt.legend(bars, modClasses)
    plt.show()

    #result = {}

    #for key in modClassCategoryCount:
    #    lst = modClassCategoryCount[key]
    #    new_dict = {}
    #    for elem in lst:
    #        elem = elem.lstrip()
    #        elem = elem.rstrip()
    #        if new_dict.has_key(elem):
    #            new_dict[elem] += 1
    #        else:
    #            new_dict[elem] = 1

    #    result[key] = new_dict


    # Metode for a plotte alle dictionary inn, med Y nedstigende rekkefolge.
    #for key in result:
    #    c = len(result[key].values())
    #    m = 1
    #    n = []
    #    print "For Modularity class:", key
    #    print sorted(result[key].items(), key=operator.itemgetter(1), reverse=True)

    #    for m in range(0,c):
    #        n.append(m)
    #        m += 1

    #    sorted_x = list(sorted(result[key].items(), key=operator.itemgetter(1), reverse=True))
    #    Labels,y = zip(*sorted_x)
    #    plt.title('Modularity Class: %s'%(key))
    #    plt.bar(n,y,1, color="blue")
    #    plt.xticks(n, Labels, rotation='vertical')
    #    plt.show()


def main(argv):
    plot(argv[0])


if __name__ == "__main__":
    main(sys.argv[1:])






