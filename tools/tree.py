import os
import sys


def generate_tree(root_dir, prefix=''):
  items = []
  try:
    items = sorted(os.listdir(root_dir),
                   key=lambda x: (not os.path.isdir(os.path.join(root_dir, x)),
                                  x))
  except PermissionError:
    return

  for index, item in enumerate(items):
    path = os.path.join(root_dir, item)
    is_last = index == len(items) - 1

    if os.path.isdir(path):
      line = prefix + ('└── ' if is_last else '├── ') + item
      print(line)
      next_prefix = prefix + ('    ' if is_last else '│   ')
      generate_tree(path, next_prefix)
    else:
      line = prefix + ('└── ' if is_last else '├── ') + item
      print(line)


if __name__ == '__main__':
  if len(sys.argv) > 1:
    # Получаем относительный путь из аргумента
    relative_path = sys.argv[1]
    # Преобразуем относительный путь в абсолютный
    start_path = os.path.abspath(relative_path)
  else:
    start_path = os.path.abspath('.')  # Текущая директория

  if not os.path.exists(start_path):
    print(f"Указанный путь не существует: {start_path}")
    sys.exit(1)

  # Выводим только конечную папку, а не полный путь
  print(os.path.basename(start_path))
  generate_tree(start_path)
