export type Page = 'users' | 'books' | 'loans';

interface NavbarProps {
  current: Page;
  onChange: (page: Page) => void;
}

const TABS: { key: Page; label: string }[] = [
  { key: 'users', label: 'Usuarios' },
  { key: 'books', label: 'Libros' },
  { key: 'loans', label: 'Préstamos' },
];

export default function Navbar({ current, onChange }: NavbarProps) {
  return (
    <header className="navbar">
      <div className="navbar-inner">
        <span className="navbar-brand">Library Manager</span>
        <nav className="navbar-tabs">
          {TABS.map((tab) => (
            <button
              key={tab.key}
              type="button"
              className={tab.key === current ? 'active' : ''}
              onClick={() => onChange(tab.key)}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </div>
    </header>
  );
}
