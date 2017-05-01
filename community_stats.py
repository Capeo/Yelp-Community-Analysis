import numpy as np
import csv
import matplotlib.pyplot as plt
import operator
import sys

def load(path):
    categoryCount = {}
    categoryModClassCount = {}
    modClassCategoryCount = {}
    modClasses = []
    modClassCount = {}
    attributeCount = {}
    attributeModClassCount = {}

    with open(path, 'r') as csvfile:
        data3 = csv.reader(csvfile, delimiter='\t')
        count = 0
        for row in data3:
            modClass = row[9]
            categories = row[8]
            categories = categories.split(",")
            categories[0] = categories[0][1:]
            categories[len(categories)-1] = categories[len(categories)-1][0:len(categories[len(categories)-1])-1]
            attributes = row[7]
            attributes = attributes.split(",")
            attributes[0] = attributes[0][1:]
            attributes[len(attributes)-1] = attributes[len(attributes)-1][0:len(attributes[len(attributes)-1])-1]
            if count > 0:
                if modClass != 'null':
                    modClass = int(modClass)
                    if not modClass in modClassCategoryCount:
                        modClassCategoryCount[modClass] = {}
                        modClasses.append(modClass)
                        modClassCount[modClass] = 1
                    else:
                        modClassCount[modClass] += 1
                    for elem in categories:
                        elem = elem.lstrip().rstrip()

                        if not elem in categoryCount:
                            categoryCount[elem] = 1
                        else:
                            categoryCount[elem] += 1

                        if not elem in categoryModClassCount:
                            categoryModClassCount[elem] = {}
                        if not modClass in categoryModClassCount[elem]:
                            categoryModClassCount[elem][modClass] = 1
                        else:
                            categoryModClassCount[elem][modClass] += 1

                    for elem in attributes:
                        elem = elem.lstrip().rstrip()

                        if not elem in attributeCount:
                            attributeCount[elem] = 1
                        else:
                            attributeCount[elem] += 1

                        if not elem in attributeModClassCount:
                            attributeModClassCount[elem] = {}
                        if not modClass in attributeModClassCount[elem]:
                            attributeModClassCount[elem][modClass] = 1
                        else:
                            attributeModClassCount[elem][modClass] += 1

            count += 1

        # Sort modularity classes, categories, and attributes by size and modularity classes by name
        modClasses = sorted(modClasses)
        sorted_categoryCount = sorted(categoryCount.items(), key=operator.itemgetter(1), reverse=True)
        sorted_attributeCount = sorted(attributeCount.items(), key=operator.itemgetter(1), reverse=True)

    return modClasses, modClassCount, categoryCount, sorted_categoryCount, categoryModClassCount, attributeCount, sorted_attributeCount, attributeModClassCount

def plot_categories(modClasses, modClassCount, categoryCount, sorted_categoryCount, categoryModClassCount, nrRestaurants):
    # Filter categories
    categories = []
    for category in sorted_categoryCount:
        if category[0] != "Restaurants" and category[1] > nrRestaurants/100.0:
            categories.append(category[0])

    # Generate modularity classes label names
    labels = []
    for modClass in modClasses:
        s = str(modClass) + ", " + str(modClassCount[modClass])
        labels.append(s)

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
    x1, x2, y1, y2 = plt.axis()
    plt.axis((x1,x2,0,1))
    plt.xticks(x, categories, rotation="vertical")
    plt.legend(bars, labels, title="Class, Count")
    plt.show()


def plot_attributes(modClasses, modClassCount, attributeCount, sorted_attributeCount, attributeModClassCount, nrRestaurants):
    # Filter categories
    attributes = []
    for attribute in sorted_attributeCount:
        if attribute[1] > nrRestaurants/100.0:
            attributes.append(attribute[0])

    # Generate modularity classes label names
    labels = []
    for modClass in modClasses:
        s = str(modClass) + ", " + str(modClassCount[modClass])
        labels.append(s)

    # Plot bar chart
    color = iter(plt.cm.rainbow(np.linspace(0,1,len(modClasses))))
    bars = []
    bottom = [0 for i in range(len(attributes))]
    width = 0.7
    x = np.arange(len(attributes))
    for modClass in modClasses:
        data = []
        for attribute in attributes:
            if modClass in attributeModClassCount[attribute]:
                data.append(float(attributeModClassCount[attribute][modClass])/float(attributeCount[attribute]))
            else:
                data.append(0)
        c = next(color)
        p = plt.bar(x, data, width, bottom=bottom, color=c)
        bars.append(p[0])
        for i in range(len(attributes)):
            bottom[i] += data[i]
    plt.title("Percentage of attributes in each community")
    plt.ylabel("Attribute of category")
    plt.xlabel("Attributes")
    x1, x2, y1, y2 = plt.axis()
    plt.axis((x1,x2,0,1))
    plt.xticks(x, attributes, rotation="vertical")
    plt.legend(bars, labels, title="Class, Count")
    plt.tight_layout()
    plt.show()


def main(argv):
    modClasses, modClassCount, categoryCount, sorted_categoryCount, categoryModClassCount, attributeCount, sorted_attributeCount, attributeModClassCount = load(argv[0])
    plot_categories(modClasses, modClassCount, categoryCount, sorted_categoryCount, categoryModClassCount,categoryCount["Restaurants"])
    plot_attributes(modClasses, modClassCount, attributeCount,sorted_attributeCount, attributeModClassCount,categoryCount["Restaurants"])


if __name__ == "__main__":
    main(sys.argv[1:])






