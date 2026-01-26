from flask import Flask, render_template, request

app = Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def index():
    percentage = None
    if request.method == 'POST':
        try:
            total_marks = float(request.form['total_marks'])
            obtained_marks = float(request.form['obtained_marks'])
            if total_marks > 0:
                percentage = (obtained_marks / total_marks) * 100
            else:
                percentage = "Total marks must be greater than zero"
        except ValueError:
            percentage = "Please enter valid numbers"
    return render_template('index.html', percentage=percentage)

if __name__ == '__main__':
    app.run(debug=True)
